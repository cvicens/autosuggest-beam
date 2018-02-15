package org.example.autosuggest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

@DefaultCoder(AvroCoder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    private String id = null;
    private String name = null;

    public Category() {
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}