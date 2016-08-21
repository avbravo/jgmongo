/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jgmongo.persistence;

import com.jgmongo.anotaciones.Id;

import com.jgmongo.anotaciones.PrimaryKey;
import com.jgmongo.util.Util;
import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.result.DeleteResult;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.descending;
import com.mongodb.client.result.UpdateResult;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 *
 * @author avbravo
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;
    private String database;
    private String collection;
    List<T> list = new ArrayList<>();
    List<PrimaryKey> primaryKeyList = new ArrayList<>();
    Exception exception;
    Util util = new Util();

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    T t1;

    protected abstract MongoClient getMongoClient();

    Integer contador = 0;

    public AbstractFacade(Class<T> entityClass, String database, String collection) {
        this.entityClass = entityClass;
        this.database = database;
        this.collection = collection;
        primaryKeyList = new ArrayList<>();
        /**
         * lee las anotaciones @Id para obtener los PrimaryKey del documento
         */
        final Field[] variables = entityClass.getDeclaredFields();
        for (final Field variable : variables) {
            final Annotation anotacion = variable.getAnnotation(Id.class);

            if (anotacion != null) {
                final Id anotacionPK = (Id) anotacion;
                PrimaryKey primaryKey = new PrimaryKey();
//                primaryKey.setName(anotacionPK.name());
//                primaryKey.setType(anotacionPK.type());
                Boolean found = false;
                for (PrimaryKey pk : primaryKeyList) {
                    if (pk.getName().equals(primaryKey.getName())) {
                        found = true;
                    }
                }
                variable.setAccessible(true);
//                  System.out.println("Nombre del atributo: " + variable.getName());
//                  System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                primaryKey.setName(variable.getName());
                primaryKey.setType(variable.getType().getName());

                // obtengo el valor del atributo
                if (!found) {
                    primaryKeyList.add(primaryKey);
                }

//                break;
            }
            //Llave primary
            if (primaryKeyList.isEmpty()) {

                exception = new Exception("No have primaryKey() ");
            }

        }
        for (PrimaryKey p : primaryKeyList) {
            System.out.println(" " + p.toString());
        }
    }

    public MongoDatabase getDB() {

        MongoDatabase db = getMongoClient().getDatabase(database);
        return db;
    }

    /**
     *
     * @return Document() correspondiente a la llave primaria
     */
    private Document getDocumentPrimaryKey(T t2) {
        Document doc = new Document();
        try {
            Object t = entityClass.newInstance();
            for (PrimaryKey p : primaryKeyList) {
                String name = "get" + util.letterToUpper(p.getName());
                Method method;
                try {
                    method = entityClass.getDeclaredMethod(name);
                    doc.put(util.letterToUpper(p.getName()), method.invoke(t2));
                } catch (Exception e) {
                    Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
                    exception = new Exception("getDocumentPrimaryKey() ", e);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "docPrimaryKey()()").log(Level.SEVERE, null, e);
            exception = new Exception("docPrimaryKey() ", e);
        }
        return doc;
    }

    /**
     * save a document
     *
     * @return
     */
    public Boolean save(T t2) {
        try {

            //verify primaryKey
            if (findById(t2) != null) {
                exception = new Exception("You can not save because there is a document that id");
                return false;
            }
            Object t = entityClass.newInstance();
            Document doc = new Document();
            Method method;
            try {
                method = entityClass.getDeclaredMethod("toDocument", entityClass);
                doc = (Document) method.invoke(t, t2);
            } catch (Exception e) {
                Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
                exception = new Exception("save() ", e);
            }
            getDB().getCollection(collection).insertOne(doc);
            return true;
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "save()").log(Level.SEVERE, null, e);
            exception = new Exception("save() ", e);
        }
        return false;
    }
    

    /**
     * removeDocument
     *
     * @param doc
     * @return
     */
    public Boolean remove(Document doc) {
        try {
            getDB().getCollection(collection).deleteOne(doc);
            return true;
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "removeDocument()").log(Level.SEVERE, null, e);
            exception = new Exception("remove() ", e);
        }
        return false;
    }

    /**
     * Remove all documment of a collection
     *
     * @return count of document delete
     */
    public Integer removeAll() {
        Integer cont = 0;
        try {
            DeleteResult dr = getDB().getCollection(collection).deleteMany(new Document());
            dr.getDeletedCount();
            cont = (int) dr.getDeletedCount();
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "removeDocument()").log(Level.SEVERE, null, e);
            exception = new Exception("removeAll() ", e);
        }
        return cont;
    }
 /**
     *
     * @param search document to search new Document("Siglas","pa")
     * @param update
     * @return
     */
    public Integer updateOne(T t2, Document doc) {
        Integer documentosModificados = 0;
        Document search = new Document();

        try {
            search = getDocumentPrimaryKey(t2);
            UpdateResult updateResult = getDB().getCollection(collection).updateOne(search, doc);
            return (int) updateResult.getModifiedCount();

        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "updateOneDocument()").log(Level.SEVERE, null, e);
            exception = new Exception("remove() ", e);
        }
        return 0;
    }
    /**
     * Busca el documento por la llave primaria
     *
     * @return T
     */
    public T findById(T t2) {

        try {
            Object t = entityClass.newInstance();
            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(getDocumentPrimaryKey(t2));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {

                    Method method;
                    try {
                        method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        t1 = (T) method.invoke(t, document);
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "find()").log(Level.SEVERE, null, e);
                        exception = new Exception("findById() ", e);
                    }

                }
            });

        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("findById() ", e);
        }
        return (T) t1;
    }

    /**
     * search document String value
     *
     * @param key
     * @param value
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public T find(String key, String value) {

        try {
            Object t = entityClass.newInstance();
            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(new Document(key, value));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {

                    Method method;
                    try {
                        method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        t1 = (T) method.invoke(t, document);
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "find()").log(Level.SEVERE, null, e);
                        exception = new Exception("find() ", e);
                    }

                }
            });

        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("find() ", e);
        }
        return (T) t1;
    }

    /**
     * search document integer value
     *
     * @param key
     * @param value
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public T find(String key, Integer value) {

        try {
            Object t = entityClass.newInstance();
            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(new Document(key, value));
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    Method method;
                    try {
                        method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        t1 = (T) method.invoke(t, document);
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "find()").log(Level.SEVERE, null, e);
                        exception = new Exception("find() ", e);
                    }
                }
            });

        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "find()").log(Level.SEVERE, null, e);
            exception = new Exception("find() ", e);
        }
        return (T) t1;
    }

    /**
     *
     * @param docSort Document para ordenar el resultado
     * @return
     */
    public List<T> findAll(Document docSort) {
        try {

            Object t = entityClass.newInstance();
            list = new ArrayList<>();
            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find().sort(docSort);
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        Method method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        list.add((T) method.invoke(t, document));
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "find()").log(Level.SEVERE, null, e);
                        exception = new Exception("findAll()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("findAll()", e);
        }
        return list;
    }

    /**
     *
     * @param doc
     * @param docSort Document of sort
     * @return
     */
    public List<T> findBy(Document doc, Document docSort) {
        try {

            Object t = entityClass.newInstance();
            list = new ArrayList<>();

            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(doc).sort(docSort);
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        Method method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        list.add((T) method.invoke(t, document));
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "findAll()").log(Level.SEVERE, null, e);
                        exception = new Exception("findBy()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("findBy() ", e);
        }
        return list;
    }
 /**
     * findLike Fuciona como el like "%s" en SQL
     *
     * @param key
     * @param value
     * @param docSort Document for sort
     * @return
     */
     public List<T> findLike(String key, String value, Document docSort) {
        list = new ArrayList<>();

        try {

            Object t = entityClass.newInstance();
            Pattern regex = Pattern.compile(value);

            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(new Document(key, regex)).sort(docSort);
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        Method method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        list.add((T) method.invoke(t, document));
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "findLike()").log(Level.SEVERE, null, e);
                        exception = new Exception("findLike()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("findLike()", e);
        }
        return list;
    }

    /**
     * Busca y ordena usando helper
     *
     * @param predicate(ascending, descending)
     * @param doc
     * @param docSort
     * @return
     */
    public List<T> findHelperSort(String predicate, Document doc, String key, String value) {
        try {

            Object t = entityClass.newInstance();
            list = new ArrayList<>();

            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = getIterable();
            switch (predicate) {
                case "ascending":
                    iterable = db.getCollection(collection).find(doc).sort(ascending(key, value));
                    break;
                case "descending":
                    iterable = db.getCollection(collection).find(doc).sort(descending(key, value));
                    break;

            }

            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        Method method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        list.add((T) method.invoke(t, document));
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "findAll()").log(Level.SEVERE, null, e);
                        exception = new Exception("findHelperSort()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("findHelperSort()", e);
        }
        return list;
    }

    /**
     *
     * @param predicate eq,gt.lt
     * @param key
     * @param value
     * @param docSort
     * @return
     */
    public List<T> helpers(String predicate, String key, String value, Document docSort) {
        try {

            Object t = entityClass.newInstance();
            list = new ArrayList<>();

            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = getIterable();
            switch (predicate) {
                case "eq":
                    iterable = db.getCollection(collection).find(eq(key, value)).sort(docSort);
                    break;
                case "lt":
                    iterable = db.getCollection(collection).find(lt(key, value)).sort(docSort);
                    break;
                case "gt":
                    iterable = db.getCollection(collection).find(gt(key, value)).sort(docSort);
                    break;
            }

            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        Method method = entityClass.getDeclaredMethod("toPojo", Document.class);
                        list.add((T) method.invoke(t, document));
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "findAll()").log(Level.SEVERE, null, e);
                        exception = new Exception("helpers()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, e);
            exception = new Exception("helpers()", e);
        }
        return list;
    }

    private FindIterable<Document> getIterable() {
        FindIterable<Document> iterable = new FindIterable<Document>() {
            @Override
            public FindIterable<Document> filter(Bson bson) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> limit(int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> skip(int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> maxTime(long l, TimeUnit tu) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> maxAwaitTime(long l, TimeUnit tu) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> modifiers(Bson bson) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> projection(Bson bson) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> sort(Bson bson) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> noCursorTimeout(boolean bln) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> oplogReplay(boolean bln) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> partial(boolean bln) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> cursorType(CursorType ct) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FindIterable<Document> batchSize(int i) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public MongoCursor<Document> iterator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Document first() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <U> MongoIterable<U> map(Function<Document, U> fnctn) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void forEach(Block<? super Document> block) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <A extends Collection<? super Document>> A into(A a) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        return iterable;
    }

   
  

    /**
     * cuenta todos los registros de un collection
     *
     * @return
     */
    public Integer count() {
        long records = 0;
        try {

            records = getMongoClient().getDatabase(database).getCollection(collection).count();
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "count()").log(Level.SEVERE, null, e);
            exception = new Exception("count()", e);
        }

        return (int) records;
    }

    /**
     * Cuenta los registros de un collection en base a la condición.
     *
     * @param doc
     * @return contador de registros en base a la condicion
     */
    public Integer count(Document doc) {
        try {
            contador = 0;

            MongoDatabase db = getMongoClient().getDatabase(database);
            FindIterable<Document> iterable = db.getCollection(collection).find(doc);

            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    try {
                        contador++;
                    } catch (Exception e) {
                        Logger.getLogger(AbstractFacade.class.getName() + "count()").log(Level.SEVERE, null, e);
                        exception = new Exception("count()", e);
                    }
                }
            });
        } catch (Exception e) {
            Logger.getLogger(AbstractFacade.class.getName() + "count()").log(Level.SEVERE, null, e);
            exception = new Exception("count()", e);
        }
        return contador;
    }
}
