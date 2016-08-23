# JGMmongo
Java Object Mapper for Mongodb
Welcome to the jgmongo wiki!
Jgmongo
Java  Object Mapper for MongoDB based in Java Driver for MongoDB, GSon

It works similar to JPA, 
<a href="https://github.com/avbravo/jgmongo/wiki">visit Wiki for Documentation</a>
# JGMongo
**1.Add to pom.xml**

    <repositories>

		<repository>

		  <id>jitpack.io</id>

		   <url>https://jitpack.io</url>

		</repository>

	</repositories>

      
    <dependencies>
         <dependency>
	    <groupId>com.github.avbravo</groupId>
	    <artifactId>jgmongo</artifactId>
	    <version>0.3</version>
	</dependency>
    </dependencies>

**2. Create beans**


@Getter

@Setter

public class Paises extends GenericBeans {

    @Id  
    
    @SerializedName("Siglas")
    
    private String siglas;
    
    @SerializedName("Pais")
    
    private String pais;
    
    @SerializedName("Logo")
    
    private String logo;
   

     public Document toDocument(Paises paises) { 
    
        return toDoc(paises);
        
    }

    public Paises toPojo(Document doc) { 
    
        return (Paises) toJava(doc, Paises.class);
        
    }

    @Override
    
    public String toString() {
    
        return "Paises{" + "siglas=" + siglas + ", pais=" + pais + '}';
        
    }
    
}


## **A. For Java Standar Edition**


## Create Facade

public class PaisesFacade extends AbstractFacade<Paises>{

    public PaisesFacade( ){
        super(Paises.class, "mydatabase", "paises");
    }

    @Override
    protected MongoClient getMongoClient() {
         MongoClient mongoClient = new MongoClient();
         return mongoClient;
    }
    
}

** Example**

   PaisesFacade paisesFacade =new PaisesFacade();
        
        Paises paises = new Paises();
        paises.setSiglas("pa");
        paises.setPais("Panama");
        paises.setLogo("");
        //save document
        paisesFacade.save(paises);
        //list all document
        List<Paises> list = paisesFacade.findAll(new Document());
        list.stream().forEach((p) -> {
            System.out.println(p.toString());
        });

## **B. For Java Enterprise Edition**
### **Create  MongoClientProvider**

@Singleton

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)

public class MongoClientProvider {

    private MongoClient mongoClient = null;
		
	@Lock(LockType.READ)
	public MongoClient getMongoClient(){	
		return mongoClient;
	}
	
	@PostConstruct
	public void init() {
            try {		
                mongoClient = new MongoClient();
            } catch (Exception e) {
                JSFUtil.addErrorMessage("init() "+e.getLocalizedMessage());
            }
				
	}
		
}

### Create Facade

@Stateless

public class PaisesFacade extends AbstractFacade<Paises> {


    @EJB

     MongoClientProvider mongoClientProvider;


     List<Paises> list = new ArrayList<>();

     Paises paises = new Paises();

   @Override

    protected MongoClient getMongoClient() {

         return mongoClientProvider.getMongoClient();

    }

    public PaisesFacade() {      

         super(Paises.class, "mydatabase", "paises");

    }

}

**Example**

@Named

@ViewScoped

public class PaisesController implements Serializable {

@Inject

PaisesFacade paisesFacade;

public Boolean save(Paises paises){

       return paisesFacade.save(paises);

}


public List<Paises> getPaises(){ 

      return paisesFacade.findAll(new Document());
}
