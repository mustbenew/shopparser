package pojo;

import java.util.HashSet;
import java.util.Objects;

public class ProductItem {
    private String articleNumber;
    private String name;
    private String brand;
    private HashSet <String> color;
    private String price;

    public ProductItem() {
    }

    public ProductItem(String articleNumber, String name, String brand, HashSet<String> color, String price) {
        this.articleNumber = articleNumber;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.price = price;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String itemCode) {
        this.articleNumber = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public HashSet <String> getColor() {
        return color;
    }

    public void setColor(HashSet<String> color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductItem that = (ProductItem) o;
        return articleNumber.equals(that.articleNumber) &&
                name.equals(that.name) &&
                brand.equals(that.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleNumber);
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "articleNumber='" + articleNumber + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public String toCSVString() {
        return (name+ ";" + brand+ ";" + color+ ";" + price+ ";" + articleNumber + "\r\n");
    }
}
