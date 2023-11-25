package dev.kurumidisciples.chisataki.secretsanta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PairingSystem {
    
    public HashMap<Long, Long> getRandomizedPairings(){
        HashMap<Long, Long> pairings = new HashMap<>();

        List<SantaStruct> santas = SantaDatabaseUtils.getAllUsers();

        if (santas == null || santas.size() < 2){
            return null;
        }

        Collections.shuffle(santas);

        for (int i = 0; i < santas.size(); i++){
            SantaStruct santa = santas.get(i);
            SantaStruct target;

            //Avoid self-pairing
            do {
                target = santas.get((i + 1) % santas.size());
            } while (santa.getUserId() == target.getUserId());

            pairings.put(santa.getUserId(), target.getUserId());
        }

        return pairings; 
    }
}
