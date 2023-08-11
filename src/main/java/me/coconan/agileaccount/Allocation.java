package me.coconan.agileaccount;

public class Allocation {
    private String name;

    public Allocation(String name) {
        assert name != null;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Allocation)) {
            return false;
        }

        Allocation other = (Allocation) obj;

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
