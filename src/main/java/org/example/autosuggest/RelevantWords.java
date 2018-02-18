/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.autosuggest;

import org.example.autosuggest.common.ExampleUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelevantWords {
  private static final Logger LOG = LoggerFactory.getLogger(RelevantWords.class);

  private static JedisPool jedisPool = null;
  //private static Jedis jedis = null;

  /**
   * Concept #2: You can make your pipeline assembly code less verbose by defining your DoFns
   * statically out-of-line. This DoFn tokenizes lines of text into individual words; we pass it
   * to a ParDo in the pipeline.
   */
  static class ExtractWordsFn extends DoFn<String, String> {
    private final Counter emptyLines = Metrics.counter(ExtractWordsFn.class, "emptyLines");
    private final Distribution lineLenDist = Metrics.distribution(
        ExtractWordsFn.class, "lineLenDistro");

    @ProcessElement
    public void processElement(ProcessContext c) {
      lineLenDist.update(c.element().length());
      if (c.element().trim().isEmpty()) {
        emptyLines.inc();
      }

      // Split the line into words.
      String[] words = c.element().split(ExampleUtils.TOKENIZER_PATTERN);

      // Output each word encountered into the output PCollection.
      for (String word : words) {
        if (!word.isEmpty()) {
          c.output(word);
        }
      }
    }
  }

  static class ExtractProductsFn extends DoFn<String, Product> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Counter emptyLines = Metrics.counter(ExtractProductsFn.class, "emptyLines");
    private final Distribution lineLenDist = Metrics.distribution(ExtractProductsFn.class, "lineLenDistro");

    @ProcessElement
    public void processElement(ProcessContext c) {
      lineLenDist.update(c.element().length());
      if (c.element().trim().isEmpty()) {
        emptyLines.inc();
      }
      
      //JSON from String to Object
      String cleanedLine = c.element().replaceAll("^\\[", "").replace(",\\s+*$", "").replaceAll(":\\s*null", ":\"\"");
      try {
        Product product = mapper.readValue(cleanedLine, Product.class);
        c.output(product);
      } catch (Throwable e) {
        System.err.println(cleanedLine + "\n" + e.getMessage());
      }
    }
  }

  static class InsertProductInCacheFn extends DoFn<Product, Product> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertProductInCacheFn.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Distribution productNameLenDist = Metrics.distribution(InsertProductInCacheFn.class, "productNameLenDistro");
    private final Counter newInserts = Metrics.counter(InsertProductInCacheFn.class, "newInserts");

    @ProcessElement
    public void processElement(ProcessContext c) {
      productNameLenDist.update(c.element().getName().length());
      
      Long newInsert = 0L;
      try (Jedis jedis = RelevantWords.jedisPool.getResource()) {
        newInsert = jedis.hset("SKUs", "sku-" + c.element().getSku(), mapper.writeValueAsString(c.element()));
        newInserts.inc(newInsert);
        LOG.debug("Inserted: " + "sku-" + c.element().getSku());
        c.output(c.element());
      } catch (Throwable e) {
        LOG.error("Error: " + c.element().getSku(), c.element(), e);
      }
    }
  }

  static class InsertSearchPrefixesInCacheFn extends DoFn<KV<String,String>, KV<String,String>> {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Counter newInserts = Metrics.counter(InsertProductInCacheFn.class, "newInserts");
    @ProcessElement
    public void processElement(ProcessContext c) {

      Long newInsert = 0L;
      try (Jedis jedis = RelevantWords.jedisPool.getResource()) {
        newInsert = jedis.zadd(c.element().getValue(), 0, "sku-" + c.element().getKey());
        newInserts.inc(newInsert);
      } catch (Throwable e) {
        System.err.println("Error inserting prefix cache SKU: " + c.element().getValue() + "<=" + c.element().getKey() + "\n" + e.getMessage());
      }

      c.output(c.element());
    }
  }

  static class ExtractSearchPrefixesFn extends DoFn<Product, KV<String,String>> {
    private final ObjectMapper mapper = new ObjectMapper();

    private final Distribution productPrefixesDistro = Metrics.distribution(ExtractSearchPrefixesFn.class, "productPrefixesDistro");
    private final Counter skippedProducts = Metrics.counter(ExtractSearchPrefixesFn.class, "skippedProducts");

    @ProcessElement
    public void processElement(ProcessContext c) {
      String productName = c.element().getName();
      productPrefixesDistro.update(productName.length());

      //Map<String, String> kv = new HashMap<String, String>();
      List<String> prefixes = new ArrayList<String>();
      for(int i = 0; i < productName.length(); i++) {
        c.output(KV.of(c.element().getSku(), productName.substring(0, i).toLowerCase()));
      }
    }
  }

  /** A SimpleFunction that converts a Word and Count into a printable string. */
  public static class FormatAsTextFn extends SimpleFunction<KV<String, Long>, String> {
    @Override
    public String apply(KV<String, Long> input) {
      return input.getKey() + ": " + input.getValue();
    }
  }

  /** A SimpleFunction that converts a Product into a printable string. */
  public static class FormatProductsAsTextFn extends SimpleFunction<Product, String> {
    @Override
    public String apply(Product input) {
      return input.getSku() + ": " + input.getName();
    }
  }

  /**
   * A PTransform that converts a PCollection containing lines of text into a PCollection of
   * formatted word counts.
   *
   * <p>Concept #3: This is a custom composite transform that bundles two transforms (ParDo and
   * Count) as a reusable PTransform subclass. Using composite transforms allows for easy reuse,
   * modular testing, and an improved monitoring experience.
   */
  public static class CountWords extends PTransform<PCollection<String>,
      PCollection<KV<String, Long>>> {
    @Override
    public PCollection<KV<String, Long>> expand(PCollection<String> lines) {

      // Convert lines of text into individual words.
      PCollection<String> words = lines.apply(
          ParDo.of(new ExtractWordsFn()));

      // Count the number of times each word occurs.
      PCollection<KV<String, Long>> wordCounts =
          words.apply(Count.<String>perElement());

      return wordCounts;
    }
  }

  /**
   * A PTransform that converts a PCollection containing lines of text into a PCollection of
   * Products
   *
   */
  public static class ExtractProducts extends PTransform<PCollection<String>, PCollection<Product>> {
    @Override
    public PCollection<Product> expand(PCollection<String> lines) {

      // Convert lines of text into individual words.
      PCollection<Product> products = lines.apply(
          ParDo.of(new ExtractProductsFn()));

      return products;
    }
  }
  public static class PrepareRedisPool extends PTransform<PCollection<Product>, PCollection<Product>> {
    private static final Logger LOG = LoggerFactory.getLogger(PrepareRedisPool.class);
    public PrepareRedisPool(String redisHost, Integer redisPort) {
      LOG.info("About to create RelevantWords.jedisPool");
      RelevantWords.jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
      LOG.info("After creating RelevantWords.jedisPool " + RelevantWords.jedisPool);
    }

    @Override
    public PCollection<Product> expand(PCollection<Product> products) {
      LOG.info("PrepareRedisPool.expand");
      return products;
    }
  }

  public static class InsertProductsInCache extends PTransform<PCollection<Product>, PCollection<Product>> {
    @Override
    public PCollection<Product> expand(PCollection<Product> products) {

      // Insert products in cache
      PCollection<Product> insertedProducts = products.apply(ParDo.of(new InsertProductInCacheFn()));

      return insertedProducts;
    }
  }

  public static class InsertSearchPrefixesInCache extends PTransform< PCollection<KV<String,String>>, 
                                                                      PCollection<KV<String,String>> > {
    @Override
    public PCollection<KV<String,String>> expand(PCollection<KV<String,String>> prefixes) {

      // Insert products in cache
      PCollection<KV<String,String>> insertedPrefixes = prefixes.apply(ParDo.of(new InsertSearchPrefixesInCacheFn()));

      return insertedPrefixes;
    }
  }

  public static class ExtractSearchPrefixes extends PTransform<PCollection<Product>, PCollection<KV<String,String>>> {
    @Override
    public PCollection<KV<String,String>> expand(PCollection<Product> products) {

      // Insert products in cache
      PCollection<KV<String,String>> searchPrefixes = products.apply(ParDo.of(new ExtractSearchPrefixesFn()));

      return searchPrefixes;
    }
  }

  /**
   * Options supported by {@link WordCount}.
   *
   * <p>Concept #4: Defining your own configuration options. Here, you can add your own arguments
   * to be processed by the command-line parser, and specify default values for them. You can then
   * access the options values in your pipeline code.
   *
   * <p>Inherits standard configuration options.
   */
  public interface RelevantWordsOptions extends PipelineOptions {

    /**
     * By default, this example reads from a public dataset containing the text of
     * King Lear. Set this option to choose a different input file or glob.
     */
    @Description("Path of the file to read from")
    @Default.String("gs://autosuggest-bucket/products.json")
    String getInputFile();
    void setInputFile(String value);

    /**
     * Set this required option to specify where to write the output.
     */
    @Description("Path of the file to write to")
    @Required
    String getOutput();
    void setOutput(String value);

    /**
     * Redis cache host
     */
    @Description("Redis Host")
    @Default.String("localhost")
    String getRedisHost();
    void setRedisHost(String value);

    /**
     * Redis cache port
     */
    @Description("Redis Port")
    @Default.Integer(6379)
    Integer getRedisPort();
    void setRedisPort(Integer value);
  }

  public static void main(String[] args) {
    RelevantWordsOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
      .as(RelevantWordsOptions.class);

    LOG.info("BEAM Redis: {}:{}", options.getRedisHost(), options.getRedisPort());
    //RelevantWords.jedisPool = new JedisPool(new JedisPoolConfig(), options.getRedisHost(), options.getRedisPort());
    //new Jedis(options.getRedisHost(), options.getRedisPort());

    Pipeline p = Pipeline.create(options);

    p.apply("ReadLines", TextIO.read().from(options.getInputFile()))
     .apply(new ExtractProducts())
     .apply(new PrepareRedisPool(options.getRedisHost(), options.getRedisPort()))
     .apply(new InsertProductsInCache())
     .apply(new ExtractSearchPrefixes())
     .apply(new InsertSearchPrefixesInCache());
     //.apply(MapElements.via(new FormatProductsAsTextFn()))
     //.apply("WriteCounts", TextIO.write().to(options.getOutput()));

    p.run().waitUntilFinish();
    //p.run();
  }
}
