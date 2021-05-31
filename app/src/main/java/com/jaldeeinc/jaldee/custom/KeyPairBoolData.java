package com.jaldeeinc.jaldee.custom;

public class KeyPairBoolData {
	private long id;
	private String name;
	private boolean isSelected;
	private Object object;
	private int viewId;
	private int nameViewId;
	private int layoutId;
	private String imagePath;


	public KeyPairBoolData() {
	}

	public KeyPairBoolData(String name, boolean isSelected) {
		this.name = name;
		this.isSelected = isSelected;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public int getNameViewId() {
		return nameViewId;
	}

	public void setNameViewId(int nameViewId) {
		this.nameViewId = nameViewId;
	}

	public int getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}