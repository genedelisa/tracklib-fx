package com.rockhoppertech.music.fx.tracklib.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by gene on 3/21/14.
 */
public class DB {
    @PersistenceContext
    private EntityManager em;


    public TrackEntity create(TrackEntity t) {
        this.em.persist(t);
        this.em.flush();
        this.em.refresh(t);
        return t;
    }

}
