package org.example.autosuggest;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Nullable;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Distribution;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheIO {
  private static final Logger LOG = LoggerFactory.getLogger(CacheIO.class);

  public static InsertProductsInCache.Builder insertProductsInCache() {
    return new InsertProductsInCache.Builder();
  }

  public static InsertSearchPrefixesInCache.Builder insertSearchPrefixesInCache() {
    return new InsertSearchPrefixesInCache.Builder();
  }

  public static class InsertProductsInCache
          extends PTransform<PCollection<Product>, PCollection<Product>> {
      
    private String host = null;
    private Integer port = null;

    @Nullable String getHost() {
      return this.host;
    }
    @Nullable Integer getPort() {
      return this.port;
    }

    private InsertProductsInCache () {
      
    }

    private InsertProductsInCache (String host, Integer port) {
      this.host = host;
      this.port = port;
    }

    public InsertProductsInCache withHost(String host) {
        return builder().setHost(host).build();
    }

    public InsertProductsInCache withPort(Integer port) {
        return builder().setPort(port).build();
    }

    public Builder builder() {
      return new Builder();
    }

    @Override
    public PCollection<Product> expand(PCollection<Product> products) {
      // Insert products in cache
      PCollection<Product> insertedProducts = products.apply(ParDo.of(new InsertProductInCacheFn()));

      return insertedProducts;
    }

    class InsertProductInCacheFn extends DoFn<Product, Product> {
      private final ObjectMapper mapper = new ObjectMapper();
      private final Distribution productNameLenDist = Metrics.distribution(InsertProductInCacheFn.class, "productNameLenDistro");
      private final Counter newInserts = Metrics.counter(InsertProductInCacheFn.class, "newInserts");
      
      private JedisPool jedisPool = null;

      @Setup
      public void setup() throws Exception {
        LOG.trace("InsertProductInCacheFn.processElement => redisHost: " + host + "redisPort: " + port);
        jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
      }

      @ProcessElement
      public void processElement(ProcessContext c) {
        productNameLenDist.update(c.element().getName().length());
        LOG.trace("InsertProductInCacheFn.processElement => redisHost: " + getHost() + "redisPort: " + getPort());
  
        Long newInsert = 0L;
        try (Jedis jedis = jedisPool.getResource()) {
          newInsert = jedis.hset("SKUs", "sku-" + c.element().getSku(), mapper.writeValueAsString(c.element()));
          newInserts.inc(newInsert);
          LOG.trace("Inserted: " + "sku-" + c.element().getSku());
          c.output(c.element());
        } catch (Throwable e) {
          LOG.error("Error: " + c.element().getSku(), c.element(), e);
        }
      }
    }

    static class Builder {
        private String host = null;
        private Integer port = null;

        private Builder() {

        }

        public Builder setHostValue(String host) {
          Builder newBuilder = new Builder();
          newBuilder.host = host;
          newBuilder.port = this.port;

          return newBuilder;
        }
    
        public Builder setPortValue(Integer port) {
          Builder newBuilder = new Builder();
          newBuilder.host = this.host;
          newBuilder.port = port;

          return newBuilder;
        }

        Builder setHost(String host) { 
          return this.setHostValue(host); }
        Builder setPort(Integer port) { 
          return this.setPortValue(port); }

        InsertProductsInCache build() {
          return new InsertProductsInCache(this.host, this.port);
        }
    }
  }
  
  public static class InsertSearchPrefixesInCache
          extends PTransform< PCollection<KV<String,String>>, PCollection<KV<String,String>> >  {
      
    private String host = null;
    private Integer port = null;

    @Nullable String getHost() {
      return this.host;
    }
    @Nullable Integer getPort() {
      return this.port;
    }

    private InsertSearchPrefixesInCache () {
      
    }

    private InsertSearchPrefixesInCache (String host, Integer port) {
      this.host = host;
      this.port = port;
    }

    public InsertSearchPrefixesInCache withHost(String host) {
        return builder().setHost(host).build();
    }

    public InsertSearchPrefixesInCache withPort(Integer port) {
        return builder().setPort(port).build();
    }

    public Builder builder() {
      return new Builder();
    }

    @Override
    public PCollection<KV<String,String>> expand(PCollection<KV<String,String>> prefixes) {

      // Insert products in cache
      PCollection<KV<String,String>> insertedPrefixes = prefixes.apply(ParDo.of(new InsertSearchPrefixesInCacheFn()));

      return insertedPrefixes;
    }

    class InsertSearchPrefixesInCacheFn extends DoFn<KV<String,String>, KV<String,String>> {
      private final ObjectMapper mapper = new ObjectMapper();
      private final Distribution productNameLenDist = Metrics.distribution(InsertSearchPrefixesInCacheFn.class, "productNameLenDistro");
      private final Counter newInserts = Metrics.counter(InsertSearchPrefixesInCacheFn.class, "newInserts");
      
      private JedisPool jedisPool = null;

      @Setup
      public void setup() throws Exception {
        LOG.trace("InsertSearchPrefixesInCacheFn.processElement => redisHost: " + host + "redisPort: " + port);
        jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
      }

      @ProcessElement
      public void processElement(ProcessContext c) {
        Long newInsert = 0L;
        try (Jedis jedis = jedisPool.getResource()) {
          newInsert = jedis.zadd(c.element().getValue(), 0, "sku-" + c.element().getKey());
          newInserts.inc(newInsert);
        } catch (Throwable e) {
          System.err.println("Error inserting prefix cache SKU: " + c.element().getValue() + "<=" + c.element().getKey() + "\n" + e.getMessage());
        }

        c.output(c.element());
      }
    }

    static class Builder {
        private String host = null;
        private Integer port = null;

        private Builder() {

        }

        public Builder setHostValue(String host) {
          Builder newBuilder = new Builder();
          newBuilder.host = host;
          newBuilder.port = this.port;

          return newBuilder;
        }
    
        public Builder setPortValue(Integer port) {
          Builder newBuilder = new Builder();
          newBuilder.host = this.host;
          newBuilder.port = port;

          return newBuilder;
        }

        Builder setHost(String host) { 
          return this.setHostValue(host); }
        Builder setPort(Integer port) { 
          return this.setPortValue(port); }

          InsertSearchPrefixesInCache build() {
          return new InsertSearchPrefixesInCache(this.host, this.port);
        }
    }
  }
}
  