package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

public class StatsDto {

        @JsonProperty("MIN")
        private String min;

        @JsonProperty("PTS")
        private int points;

        @JsonProperty("REB")
        private int rebounds;

        @JsonProperty("AST")
        private int assists;

        @JsonProperty("STL")
        private int steals;

        @JsonProperty("BLK")
        private int blocks;

        private int fgm;
        private int fga;
        private double fgp;

        @JsonProperty("FG")
        private String fg;
        private String toFG() {
            return fgm + "/" + fga;
        }

        private int tpm;
        private int tpa;
        private double tpp;

        @JsonProperty("3PT")
        private String tpt;
        private String to3PT() {
            return tpm + "/" + tpa;
        }

        private int ftm;
        private int fta;
        private double ftp;

        @JsonProperty("FT")
        private String ft;
        private String toFT() {
            return ftm + "/" + fta;
        }

        private int offReb;
        private int defReb;

        @JsonProperty("TO")
        private int turnovers;

        @JsonProperty("PF")
        private int fouls;

        @JsonProperty("+/-")
        private int plusMinus;

    public StatsDto(Stats stats) {
        this.min = stats.getMin();
        this.points = stats.getPoints();
        this.rebounds = stats.getRebounds();
        this.assists = stats.getAssists();
        this.steals = stats.getSteals();
        this.blocks = stats.getBlocks();
        this.fgm = stats.getFgm();
        this.fga = stats.getFga();
        this.fgp = stats.getFgp();
        this.tpm = stats.getTpm();
        this.tpa = stats.getTpa();
        this.tpp = stats.getTpp();
        this.ftm = stats.getFtm();
        this.fta = stats.getFta();
        this.ftp = stats.getFtp();
        this.offReb = stats.getOffReb();
        this.defReb = stats.getDefReb();
        this.turnovers = stats.getTurnovers();
        this.fouls = stats.getFouls();
        this.plusMinus = stats.getPlusMinus();

        this.fg = toFG();
        this.ft = toFT();
        this.tpt = to3PT();
    }

    public static StatsDto convertToDto(Stats stats) {
        return stats == null ? null : new StatsDto(stats);
    }
}
