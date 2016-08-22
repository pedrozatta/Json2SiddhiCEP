package br.produban.bdm.commons;

import java.util.List;

public class Region<T> {

	private String name;
	private List<T> list;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}