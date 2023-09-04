package ru.pazik98.db;

import org.bukkit.Chunk;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.pazik98.entity.SoilState;

public class ManagerDAO {
//    public SoilStateData findById(Chunk chunk) {
//        return HibernateSessionFactory.getSessionFactory().openSession().get(SoilStateData.class, id);
//    }

    public void save(SoilStateData soilStateData) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tr = session.beginTransaction();
        session.save(soilStateData);
        tr.commit();
        session.close();
    }
}
