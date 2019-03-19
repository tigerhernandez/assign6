/*
 * The MIT License
 *
 * Copyright 2019 Len Payne <len.payne@lambtoncollege.ca>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jaxrs;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@Path("productCode")
@ApplicationScoped
public class ProductCodeREST {

    @PersistenceContext(unitName = "buildit13PU")
    private EntityManager em;
    
    @Inject
    private UserTransaction transaction;

    // http://localhost:8080/CSD4464-JAX-RS-2019W/api/productCode
    /**
     * Uses a JPA Query to return the entire list as JSON.
     * @return List of ProductCodes
     */
    @GET
    @Produces({"application/json"})
    public List<ProductCode> getAll() {
        List<ProductCode> productCodes = em.createQuery("SELECT p FROM ProductCode p").getResultList();
        return productCodes;
    }

    /**
     * Uses a JPA Named Query to return a specific entity as a list.
     * @param id the Product Code (ID)
     * @return 
     */
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public List<ProductCode> getOne(@PathParam("id") String id) {
        Query q = em.createNamedQuery("findOne");
        q.setParameter("prodCode", id);
        List<ProductCode> productCodes = q.getResultList();
        return productCodes;
    }

    /**
     * Saves an object received as a JSON payload.
     * @param productCode 
     */
    @POST
    @Consumes("application/json")
    public void addOne(ProductCode productCode) {
        try {
            transaction.begin();
            em.persist(productCode);
            transaction.commit();
        } catch (Exception ex) {
            Logger.getLogger(ProductCodeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Updates an existing Product Code based on an incoming JSON payload.
     * @param productCode
     * @param id 
     */
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void editOne(ProductCode productCode, @PathParam("id") String id) {
        try {
            Query q = em.createQuery("SELECT p FROM ProductCode p WHERE p.prodCode = :id");
            q.setParameter("id", id);
            ProductCode savedPC = (ProductCode) q.getSingleResult();
            savedPC.setDescription(productCode.getDescription());
            savedPC.setDiscountCode(productCode.getDiscountCode());
            transaction.begin();
            em.merge(savedPC);
            transaction.commit();
        } catch (Exception ex) {
            Logger.getLogger(ProductCodeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Finds and deletes an existing record using the JPA's find method.
     * @param id 
     */
    @DELETE
    @Path("{id}")
    public void deleteOne(@PathParam("id") String id) {
        try {
            transaction.begin();
            ProductCode found = em.find(ProductCode.class, id);
            em.remove(found);
            transaction.commit();
        } catch (Exception ex) {
            Logger.getLogger(ProductCodeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
