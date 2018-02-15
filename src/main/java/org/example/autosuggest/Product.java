package org.example.autosuggest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    private String sku = null;
    private String name = null;
    @Nullable private String type = null;
    @Nullable private String price = null;
    @Nullable private String upc = null;
    private Category[] category = null;
    @Nullable private String shipping = null;
    @Nullable private String description = null;
    @Nullable private String manufacturer = null;
    @Nullable private String model = null;
    @Nullable private String url = null;
    @Nullable private String image = null;

    public Product() {
    }

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public Category[] getCategory() {
		return category;
	}

	public void setCategory(Category[] category) {
		this.category = category;
	}

	public String getShipping() {
		return shipping;
	}

	public void setShipping(String shipping) {
		this.shipping = shipping;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}