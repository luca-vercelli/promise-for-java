package com.github.fetch;

import java.util.ArrayList;
import java.util.List;

public class PublicApiListBean {

	private Integer count;
	private List<PublicApiBean> entries = new ArrayList<>();

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<PublicApiBean> getEntries() {
		return entries;
	}

	public void setEntries(List<PublicApiBean> entries) {
		this.entries = entries;
	}
}
