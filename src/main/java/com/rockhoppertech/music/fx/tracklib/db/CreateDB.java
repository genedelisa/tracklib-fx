package com.rockhoppertech.music.fx.tracklib.db;

import com.rockhoppertech.music.midi.js.Instrument;
import com.rockhoppertech.music.midi.js.MIDITrack;
import com.rockhoppertech.music.midi.js.MIDITrackBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gene on 3/21/14.
 */

public class CreateDB {
    private static final String PERSISTENCE_UNIT_NAME = "tracklib";
    private static EntityManagerFactory factory;

    public static void main(String... args) {
        TrackEntity track = new TrackEntity();
        MIDITrack mt = MIDITrackBuilder.create()
                .name("some track")
                .description("this is the description")
                .noteString("c d Ef cs3 f#5 e f g")
                .durations(1d, 1.5, .5)
                .instrument(Instrument.TRUMPET)
                .sequential()
                .build();
        track.setTrack(mt);


        Map<String, String> factoryProperties = new HashMap<>();
        factoryProperties.put("javax.persistence.jdbc.url", "jdbc:derby:/Users/gene/databases/tracks;create=true");
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, factoryProperties);

        EntityManager em = factory.createEntityManager();

        Query q = em.createQuery("select t from TrackEntity t");
        List<TrackEntity> trackList = q.getResultList();
        for (TrackEntity t : trackList) {
            System.out.println(t.getMusicXML());
        }
        System.out.println("Size: " + trackList.size());


        em.getTransaction().begin();
        em.persist(track);
        em.getTransaction().commit();

        em.close();
    }
}
