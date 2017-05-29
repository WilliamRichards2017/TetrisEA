import java.io.*;
import java.util.*;

public class TetrisEA{

    // parameter used for evolutionary agorithm
    private static int pop_size;
    private static int num_generations;
    private static int T_size;
    private static int elites;
    private static int max_fitness;
    private static double mutation_prob;
    private static double mutation_range;
    private static double crossover_prob;
    private static String selection_type;   // Tournament or Porportional w/ Universal Stochastic

    public static void main(String[] args){
        // read parameters
        readParams("params.txt");

        // STUB TO IMPLEMENT
        runEA();
    }

    // crossover operator
    public static void crossoverPop(Player[] pop){
        ArrayDeque<Player> queue = new ArrayDeque<Player>();
        for(Player p : pop){
            double prob = Math.random();
            if(prob <= crossover_prob){
                queue.offer(p);
            }
            if(queue.size() == 2){
                Player p1 = queue.poll();
                Player p2 = queue.poll();
                p1.crossover(p2);
            }
        }
    }

    public static void runEA(){

    }

    // mutation operator
    public static void mutatePop(Player[] pop){
        for(Player p : pop){
            double random = Math.random();
            if(random <= mutation_prob){
                p.mutate(mutation_range);
            }
        }
    }

    public static Player[] tournamentSelection(Player[] pop, int[] fitnesses){
        Player[] nextGeneration = new Player[pop_size];
        int index = 0;

        for(int i = 0; i < pop_size; i++){
            // shuffle population & their fitnesses
            shuffle(pop, fitnesses);

            // pick first T_size individuals
            Player best_p = pop[0];
            int best_f = fitnesses[0];
            for(int j = 1; j < T_size; j++){
                if(fitnesses[j] > best_f){
                    best_f = fitnesses[j];
                    best_p = pop[j];
                }
            }

            // store the best indidivudla from a tournament
            nextGeneration[index++] = best_p;
        }
        return nextGeneration;
    }

    // proportional selection with universal stochastic sampling
    public static Player[] universalStochasticSampling(Player[] pop, int[] fitnesses){
      return null;

    }

    // randomly shuffle an array
    private static void shuffle(Player[] p, int[] f){
        int index;
        Player temp_p;
        int temp_f;
        Random random = new Random();
        for(int i = p.length-1; i > 0; i--){
            index = random.nextInt(i+1);

            // swap player
            temp_p = p[index];
            p[index] = p[i];
            p[i] = temp_p;

            // swap corresponding fitnesses
            temp_f = f[index];
            f[index] = f[i];
            f[i] = temp_f;
        }
    }

    // read parameters necessary for evolution
    private static void readParams(String fileName){
        try{
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            int row = 0;
            while(scan.hasNextLine()){
                String[] line = scan.nextLine().split(" ");
                switch(row){
                    case 0:
                        pop_size = Integer.parseInt(line[1]);
                        break;
                    case 1:
                        num_generations = Integer.parseInt(line[1]);
                        break;
                    case 2:
                        selection_type = line[1].toLowerCase();
                        break;
                    case 3:
                        T_size = Integer.parseInt(line[1]);
                        break;
                    case 4:
                        elites = Integer.parseInt(line[1]);
                        break;
                    case 5:
                        mutation_prob = Double.parseDouble(line[1]);
                        break;
                    case 6:
                        mutation_range = Double.parseDouble(line[1]);
                        break;
                    case 7:
                        crossover_prob = Double.parseDouble(line[1]);
                        break;
                    case 8:
                        max_fitness = Integer.parseInt(line[1]);
                    default:
                        break;
                }
                row++;
            }
        }catch(IOException e){
            System.out.println("Error reading in parameters...");
        }
    }
}
