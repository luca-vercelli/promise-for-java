package com.github.fetch;

public class PublicApiBean {

	private String API;
	private String description;
	private Boolean HTTPS;
	private String cors;
	private String link;
	private String category;

	public String getAPI() {
		return API;
	}

	public void setAPI(String aPI) {
		API = aPI;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getHTTPS() {
		return HTTPS;
	}

	public void setHTTPS(Boolean hTTPS) {
		HTTPS = hTTPS;
	}

	public String getCors() {
		return cors;
	}

	public void setCors(String cors) {
		this.cors = cors;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
