package com.example.amortizacion.models;

public class AmortizacionItem {
    private int numero;
    private String tea;
    private String tes;
    private String plazoGracia;
    private double saldoInicial;
    private double interes;
    private double cuota;
    private double amortizacion;
    private double saldoFinal;

    public AmortizacionItem() {}

    public AmortizacionItem(int numero, String tea, String tes, String plazoGracia, 
                           double saldoInicial, double interes, double cuota, 
                           double amortizacion, double saldoFinal) {
        this.numero = numero;
        this.tea = tea;
        this.tes = tes;
        this.plazoGracia = plazoGracia;
        this.saldoInicial = saldoInicial;
        this.interes = interes;
        this.cuota = cuota;
        this.amortizacion = amortizacion;
        this.saldoFinal = saldoFinal;
    }

    // Getters and Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getTea() { return tea; }
    public void setTea(String tea) { this.tea = tea; }

    public String getTes() { return tes; }
    public void setTes(String tes) { this.tes = tes; }

    public String getPlazoGracia() { return plazoGracia; }
    public void setPlazoGracia(String plazoGracia) { this.plazoGracia = plazoGracia; }

    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }

    public double getInteres() { return interes; }
    public void setInteres(double interes) { this.interes = interes; }

    public double getCuota() { return cuota; }
    public void setCuota(double cuota) { this.cuota = cuota; }

    public double getAmortizacion() { return amortizacion; }
    public void setAmortizacion(double amortizacion) { this.amortizacion = amortizacion; }

    public double getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(double saldoFinal) { this.saldoFinal = saldoFinal; }
}
