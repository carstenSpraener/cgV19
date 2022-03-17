package de.csp.demo.rest.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.beans.Customizer;
import java.sql.Date;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;

public class UserJPATests {
    private EntityManagerFactory emf;
    private int groupCount = 1;

    @Before
    public void before() throws Exception {
        emf = Persistence.createEntityManagerFactory("junit-test");
    }

    protected Object runInTransaction(Function<EntityManager, Object> emConsumer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Object result = emConsumer.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    public void testCreateAndFindByID() {
        EntityManager em = emf.createEntityManager();
        User u = createNewUser();
        assertNull(u.getId());
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
        assertNotNull(u.getId());
        em.close();

        em = emf.createEntityManager();
        User u2 = em.find(User.class, u.getId());
        assertNotNull(u2);
    }

    @Test
    public void testUpdate() {
        User u = createNewUser();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
        em.close();

        em = emf.createEntityManager();
        em.getTransaction().begin();
        Long uID = u.getId();
        u = em.find(User.class, u.getId());
        u.setVorname("Vorname 2");
        em.persist(u);
        em.getTransaction().commit();
        em.close();
        assertEquals(uID, u.getId());
        assertEquals("Vorname 2", u.getVorname());

        em = emf.createEntityManager();
        u = em.find(User.class, uID);
        em.close();
        assertEquals("Vorname 2", u.getVorname());
    }

    @Test
    public void testDelete() throws Exception {
        User u = createNewUser();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
        assertNotNull(u.getId());
        em.close();

        Long uID = u.getId();
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(User.class, uID));
        em.getTransaction().commit();

        em = emf.createEntityManager();
        em.getTransaction().begin();
        u = em.find(User.class, uID);
        em.getTransaction().commit();
        em.close();
        assertNull(u);
    }

    @Test
    public void testUserWithAdressPersist() throws Exception {
        User u = (User)
                runInTransaction((em) -> {
                    User myU = createNewUser();
                    addAdress(myU);
                    addAdress(myU);
                    em.persist(myU);
                    return myU;
                });
        final Long uID = u.getId();

        assertNotNull(u.getId());
        assertEquals(2, u.getAdressen().size());
        for (Adresse a : u.getAdressen()) {
            assertNotNull(a.getId());
            assertEquals(u, a.getUser());
        }

        u = (User) runInTransaction((e) -> {
            User myU = e.find(User.class, uID);
            Adresse a = myU.getAdressen().iterator().next();
            myU.removeAdresse(a);
            return myU;
        });

        runInTransaction((e) -> {
            User myU = e.find(User.class, uID);
            assertEquals(1, myU.getAdressen().size());
            return null;
        });
    }

    @Test
    public void testUserAndGroup() throws Exception {
        User u = (User)runInTransaction(e->{
            Customer myU = createNewCustomer();
            Group g1 = createGroup();
            Group g2 = createGroup();
            myU.addGroup(g1);
            myU.addGroup(g2);
            e.persist(myU);
            return myU;
        });
        final Long uID = u.getId();
        assertNotNull(uID);
        final Long gID = u.getGroups().iterator().next().getId();
        assertNotNull(gID);
        final Long g2ID = ((Group) u.getGroups().toArray()[1]).getId();
        assertNotNull(g2ID);

        runInTransaction(e->{
            User myU = e.find(User.class, uID);
            assertNotNull(myU);
            assertTrue(myU instanceof Customer);
            assertEquals(2, myU.getGroups().size());
            return null;
        });
        runInTransaction(e->{
            Group myG = e.find(Group.class, gID);
            assertNotNull(myG);
            assertEquals(1, myG.getUsers().size());
            myG = e.find(Group.class, g2ID);
            assertNotNull(myG);
            assertEquals(1, myG.getUsers().size());
            return null;
        });

        // Remove Group2 from user
        runInTransaction(e->{
            User myU = e.find(User.class, uID);
            Group g = e.find(Group.class, g2ID);
            myU.getGroups().remove(g);
            return myU;
        });
        // check, that group is removed but still in database
        runInTransaction(e->{
            User myU = e.find(User.class, uID);
            Group g2 = e.find(Group.class, g2ID);
            assertNotNull(g2);
            assertEquals(1, myU.getGroups().size());
            assertFalse(myU.getGroups().contains(g2));
            return myU;
        });
    }

    private Customer createNewCustomer() {
        Customer c = new Customer();
        c.setCustomerValue(100);
        c.setName("customer");
        c.setEmail("customer@rest.demo");
        c.setVorname("new");
        c.setGebDat(new Date(System.currentTimeMillis()));
        c.setIsVeryfied(true);
        return c;
    }

    private User createNewUser() {
        User u = new User();
        u.setName("name");
        u.setEmail("email");
        u.setGebDat(new Date(System.currentTimeMillis()));
        u.setVorname("vorname");
        u.setIsVeryfied(false);
        return u;
    }

    private User addAdress(User u) {
        u.addAdresse(createAdress());
        return u;
    }

    private Adresse createAdress() {
        Adresse a = new Adresse();
        a.setHausnr("10a-c");
        a.setPlz("D-49594");
        a.setStrasse("Kirchesch");
        a.setOrt("Alfhausen");
        return a;
    }

    private Group createGroup() {
        Group g = new Group();
        g.setName("gruppe-"+(groupCount++));
        return g;
    }
}
