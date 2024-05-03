/* CoconanBY (C)2024 */
package me.coconan.agileaccount;

import lombok.Getter;

@Getter
public class Allocation {
    private final String name;

    public Allocation(String name) {
        assert name != null;
        this.name = name;
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
