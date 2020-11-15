/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.calltomemory;

/**
 *
 * @author User
 */
public class MyNode {
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public String getString() {
        return string;
    }
    private int indexX;
    private int indexY;
    private String string;
    
    public void writeAll(){
        System.out.println("index: " + index + ", indexX: " + indexX + ", indexY: " + indexY + ", string: " + string);
    }
}
