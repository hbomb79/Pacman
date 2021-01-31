package controllers;

import main.PacmanGame;

public abstract class Controller {
    protected PacmanGame gameInstance;
    public Controller(PacmanGame g) {
        gameInstance = g;
    }
}
