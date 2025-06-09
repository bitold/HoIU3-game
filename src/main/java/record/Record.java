package record;

import java.io.Serializable;

public class Record implements Serializable {
    private String nickname;
    private String description;
    private boolean victory;

    /**
    Запись-рекорд. Рекорд имеет никнейм игрока и логическое значение, определяющее, победу или поражение он засчитывает.
     */
    Record(String nickname, boolean hasWon){
        this.nickname = nickname;
        this.victory = hasWon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isVictory() {
        return victory;
    }
}
