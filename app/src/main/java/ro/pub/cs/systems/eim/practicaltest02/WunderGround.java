package ro.pub.cs.systems.eim.practicaltest02;

public class WunderGround {
    private String nume;

    public WunderGround(){
        this.nume = null;
    }
    public WunderGround(String nume){
        this.nume = nume;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String toString() {
        return "WunderGround{" +
                "nume='" + nume + '\'' +
                '}';
    }
}
