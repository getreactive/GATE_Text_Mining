package com.getreactive.acme.DtoClasses;

public class ProductDto {
	
	/*
	 * This class defines an object type to store Product Info
	 */
	String product;
	String version;
	String type;
	String kind;
	String url;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
    public boolean equals(Object object)
    {
		ProductDto dto=(ProductDto) object;
            if(dto.product.equals(this.product))
                return true;
                    return false;
    }
    
    @Override
    public int hashCode() {
      int hash = 0;
      hash = hash + this.product.hashCode();
    
      return hash;
    }
}
