package fr.wikitahiti.teiki.wikitahiti.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "RATES".
 */
@Entity
public class Rates {

    @Id(autoincrement = true)
    private Long id;
    private int rate_id;
    private Integer difficulty;
    private Integer endurance;
    private Integer dangerous;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated(hash = 1355940387)
    public Rates() {
    }

    public Rates(Long id) {
        this.id = id;
    }

    @Generated(hash = 2078287614)
    public Rates(Long id, int rate_id, Integer difficulty, Integer endurance, Integer dangerous) {
        this.id = id;
        this.rate_id = rate_id;
        this.difficulty = difficulty;
        this.endurance = endurance;
        this.dangerous = dangerous;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRate_id() {
        return rate_id;
    }

    public void setRate_id(int rate_id) {
        this.rate_id = rate_id;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEndurance() {
        return endurance;
    }

    public void setEndurance(Integer endurance) {
        this.endurance = endurance;
    }

    public Integer getDangerous() {
        return dangerous;
    }

    public void setDangerous(Integer dangerous) {
        this.dangerous = dangerous;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}