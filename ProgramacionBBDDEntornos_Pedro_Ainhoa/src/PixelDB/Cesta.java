package PixelDB;

import java.util.ArrayList;
import java.util.List;


public class Cesta {
    

     //Clase para representar un objeto en la cesta

    public static class Objeto {
        private String modelo;
        private String opcion;
        private String color;
        private double precio;
        

        public Objeto(String modelo, String opcion, String color, double precio) {
            this.modelo = modelo;
            this.opcion = opcion;
            this.color = color;
            this.precio = precio;
        }
        
       //Getters
        public String getModelo() {
            return modelo;
        }
        

        public String getOpcion() {
            return opcion;
        }
        

        public String getColor() {
            return color;
        }
        

        public double getPrecio() {
            return precio;
        }
        
       //toString
        @Override
        public String toString() {
            return modelo + " - " + opcion + " - " + color + " - " + String.format("%.2f €", precio);
        }
    }
    
    private List<Objeto> Objetos;


    public Cesta() {
        this.Objetos = new ArrayList<>();
    }
    
    //añadir un objeto a la cesta
    public void addItem(Objeto Objeto) {
        Objetos.add(Objeto);
    }
    
    //Eliminar un objeto de la cesta
    public void removeItem(int index) {
        if (index >= 0 && index < Objetos.size()) {
            Objetos.remove(index);
        }
    }
    
    //Obtener una lista de los objetos de la cesta
    public List<Objeto> Objetos() {
        return Objetos;
    }
    

    public int getConteoObjetos() {
        return Objetos.size();
    }
    
   //Obtener el precio total de
    public double getTotalPrecio() {
        double total = 0.0;
        for (Objeto item : Objetos) {
            total += item.getPrecio();
        }
        return total;
    }
    
    //limpiar la cesta
    public void clear() {
        Objetos.clear();
    }
    
    //Comprueba que la cesta este vacia
    public boolean isEmpty() {
        return Objetos.isEmpty();
    }
}