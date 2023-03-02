package com.lr.platform.utils;

public class Score {

    private static final Integer decay=25;

    public static Integer CalculateCurrentScore(Integer rawScore,Integer solve){
        if (solve==0){
            return rawScore;
        }
        if (solve>=decay){
            double sc=0.1*rawScore;
            return (int) sc;
        }
        double sc=Math.ceil((0.001*rawScore-rawScore)/(decay*decay)*solve*solve+rawScore);
        return (int) sc;
    }

    public static Integer CalculateCurrentScoreTest(Integer rawScore,Float solve){
        if (solve>=decay){
            double sc=0.1*rawScore;
            return (int) sc;
        }
        Double weight=(-decay)/(1-Math.E);
        Double degree= (double) ((solve.floatValue() / decay.floatValue()));
        //Double degree=Math.log((-decay.floatValue())/(solve.floatValue()+weight)+Math.E);
        //Double lowerLimit= 100 / rawScore.doubleValue();
        Double maxDegree=Math.acos(0.1);
        degree=degree*maxDegree;
        weight=(maxDegree-degree)*Math.log((-decay.floatValue())/(solve.floatValue()+weight)+Math.E);
        degree=degree+Math.sqrt(decay)*(weight*weight*weight*0.6);
        //=degree+Math.sqrt(Math.sqrt(decay))*(weight*weight);
        degree=Math.cos(degree);
        double sc=Math.ceil(degree*rawScore);
        return (int) sc;
    }

    public static void main(String[] args){
        for (int i=0;i<100;i++){
            System.out.println(CalculateCurrentScoreTest(1500,i*.5f));
        }
    }
}
