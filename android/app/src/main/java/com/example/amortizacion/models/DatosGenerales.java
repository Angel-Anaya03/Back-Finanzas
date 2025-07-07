package com.example.amortizacion.models;

public class DatosGenerales {
    private double precioVenta;
    private double cuotaInicial;
    private double prestamo;
    private String frecuencia;
    private int numeroAnios;
    private int numeroPeriodos;

    public DatosGenerales() {}

    public DatosGenerales(double precioVenta, double cuotaInicial, double prestamo, 
                         String frecuencia, int numeroAnios, int numeroPeriodos) {
        this.precioVenta = precioVenta;
        this.cuotaInicial = cuotaInicial;
        this.prestamo = prestamo;
        this.frecuencia = frecuencia;
        this.numeroAnios = numeroAnios;
        this.numeroPeriodos = numeroPeriodos;
    }

    // Getters and Setters
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public double getCuotaInicial() { return cuotaInicial; }
    public void setCuotaInicial(double cuotaInicial) { this.cuotaInicial = cuotaInicial; }

    public double getPrestamo() { return prestamo; }
    public void setPrestamo(double prestamo) { this.prestamo = prestamo; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public int getNumeroAnios() { return numeroAnios; }
    public void setNumeroAnios(int numeroAnios) { this.numeroAnios = numeroAnios; }

    public int getNumeroPeriodos() { return numeroPeriodos; }
    public void setNumeroPeriodos(int numeroPeriodos) { this.numeroPeriodos = numeroPeriodos; }
}
