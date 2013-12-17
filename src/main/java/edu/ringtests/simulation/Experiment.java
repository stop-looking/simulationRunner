package edu.ringtests.simulation;

/**
 * Class describes single experimental sample - final dimensions reduction and its name.
 *
 * @author Kamil Sikora
 *         Data: 17.12.13
 */
public class Experiment {
    private String name;

    /**
     * Per cent of final to initial height ratio.
     */
    private double height;
    /**
     * Per cent of final to initial inner diameter ratio.
     */
    private double innerDiameter;

    /**
     * Friction factor computed from simulation.
     */
    private double frictionFactor;

    public Experiment(String name, double height, double innerDiameter) {
        this.name = name;
        this.height = height;
        this.innerDiameter = innerDiameter;
        this.frictionFactor = -1;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public double getInnerDiameter() {
        return innerDiameter;
    }

    public double getFrictionFactor() {
        return frictionFactor;
    }

    public void setFrictionFactor(double frictionFactor) {
        this.frictionFactor = frictionFactor;
    }

    @Override
    public String toString() {
        return String.format("%s - height: %f - width: %f", name, height, innerDiameter);
    }
}
