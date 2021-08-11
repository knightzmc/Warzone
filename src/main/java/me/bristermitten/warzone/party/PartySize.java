package me.bristermitten.warzone.party;

public enum PartySize {
    SINGLES(1),
    DOUBLES(2),
    SQUADS(4);

    private final int size;

    PartySize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
