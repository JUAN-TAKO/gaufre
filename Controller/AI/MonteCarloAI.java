package Controller.AI;
import Controller.AIPlayer;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

import Model.Grid;
import utils.Pair;

public class MonteCarloAI extends AIPlayer{
    
    private HashMap<Integer, Pair<ArrayList<Pair<Integer, Float>>, Integer>> graph;
    private float difficulty = 1.0f;

    public MonteCarloAI(Grid g, float difficulty){
        super(g);
        this.graph = new HashMap<>();
        this.difficulty = 1.0f + difficulty/5.0f;
    }

    public int getNextPlay(Grid g){
        
        ArrayList<Pair<Integer, Float>> weights;
        
        int code = g.data().hashCode();

        if(!this.graph.containsKey(code)){
            evalGrid(g, 1);
        }
        weights = this.graph.get(code).getKey();
        float sum = 0.0f;
        for(Pair<Integer, Float> p : weights){
            double tmp = Math.pow(this.difficulty, -p.getValue().doubleValue());
            sum += tmp;
            System.out.println("[" + p.getKey() % g.width() + ", " + p.getKey() / g.width() + "] : " + tmp + " (" + p.getValue().doubleValue() + ")");
        }
        System.out.println("total: " + sum);

        float rand = (float)Math.random() * sum;
        float cursor = 0.0f;
        for(Pair<Integer, Float> p : weights){
            cursor += Math.pow(this.difficulty, -p.getValue().doubleValue());
            if(cursor >= rand){
                System.out.println("choisi: [" + p.getKey() % g.width() + ", " + p.getKey() / g.width() + "]");
                return p.getKey();
            }
        }
        return 0;
    }

    private float evalGrid(Grid g, int index){
        if(index == 0){
            return 1;
        }
        int code = g.data().hashCode();
        int sum = 0;
        
        if(this.graph.containsKey(code)){
            for(Pair<Integer, Float> link : this.graph.get(code).getKey()){
                sum -= link.getValue();
            }
        }
        else{
            ArrayList<Pair<Integer, Grid>> next = this.GenerateNext(g);
            ArrayList<Pair<Integer, Float>> weights = new ArrayList<>();
            for(Pair<Integer, Grid> grid_p : next){
                Float w = new Float(evalGrid(grid_p.getValue(), grid_p.getKey()));
                sum -= w;
                weights.add(new Pair<>(grid_p.getKey(), w));
            }

            weights.sort(Comparator.comparing(p -> p.getValue()));

            this.graph.put(code, new Pair<>((weights), next.size()));
        }

        return sum;
    }

	protected boolean tempsEcoule() {
		int nextPlay = getNextPlay(this.g);
		System.out.println("L'IA a joué : x = " + nextPlay % g.width() + " y = " + nextPlay / g.width());
		this.g.play(nextPlay % g.width(), nextPlay / g.width());
        return true;
    }

}