package com.programmer.dataseef.Models;

import com.google.gson.annotations.SerializedName;

public class Home {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String nombre;
    @SerializedName("garage")
    private int garage;
    @SerializedName("price")
    private int price;
    @SerializedName("phone")
    private int phone;
    @SerializedName("address")
    private String address;
    @SerializedName("x_coordinate")
    private double x_coordinate;
    @SerializedName("y_coordinate")
    private double y_coordinate;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("image_one")
    private String image_one;
    @SerializedName("image_two")
    private String image_two;
    @SerializedName("image_three")
    private String image_three;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getGarage() {
        return garage;
    }

    public void setGarage(int garage) {
        this.garage = garage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getX_coordinate() {
        return x_coordinate;
    }

    public void setX_coordinate(double x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    public double getY_coordinate() {
        return y_coordinate;
    }

    public void setY_coordinate(double y_coordinate) {
        this.y_coordinate = y_coordinate;
    }

    public String getImage_one() {
        return image_one;
    }

    public void setImage_one(String image_one) {
        this.image_one = image_one;
    }

    public String getImage_two() {
        return image_two;
    }

    public void setImage_two(String image_two) {
        this.image_two = image_two;
    }

    public String getImage_three() {
        return image_three;
    }

    public void setImage_three(String image_three) {
        this.image_three = image_three;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
