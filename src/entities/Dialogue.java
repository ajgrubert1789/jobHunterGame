package entities;

public class Dialogue {

    private String[]lines;
    private int currentLine = 0;



    public Dialogue(String[] lines) {
        this.lines = lines;
    }

    public String getNextLine(){
        if(currentLine < lines.length){
            return lines[currentLine++];
        }
        return null;
    }

    public void reset(){
        currentLine = 0;
    }

    public void update(){

    }
}
