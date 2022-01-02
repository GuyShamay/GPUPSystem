package main;

import main.app.GPUPApp;
import engine.GPUPEngine;

public class Program {
    public static void main(String[] args) {
        GPUPApp app = new GPUPApp();
        app.init(new GPUPEngine());
        app.run();
    }
}
