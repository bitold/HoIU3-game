package event;
import java.io.Serializable;


public class Event implements Serializable{

    boolean active;
    int duration;
    int movesLeft;

    Event(){

    }

    void activate(){
        setActive(true);
        movesLeft = duration;
    }

    public void completeIteration(){
        if (movesLeft > 0){
            movesLeft = movesLeft-1;
        }
        endIfNoMovesLeft();
    }

    void endIfNoMovesLeft(){
        if (movesLeft <= 0){
            active = false;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }

    public int getDuration() {
        return duration;
    }

    public int getMovesLeft() {
        return movesLeft;
    }
}
