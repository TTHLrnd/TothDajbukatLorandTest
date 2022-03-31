package ABT.TothDajbukatLorand.services;

import ABT.TothDajbukatLorand.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbServiceTest {

    private final String DATA_SOURCE_URL = "datasource_test.txt";
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/sqlmassdata_test";
    private final String DATABASE_USERNAME = System.getenv("DB_USER");
    private final String DATABASE_PASSWORD = System.getenv("DB_PASS");
    Connection conn;
    private List<Project> expectedList;

    public DbServiceTest() {
        this.conn = connect();
    }

    private Connection connect(){
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    //Reading the file, and calling the addToProjects to populate the DB
    @BeforeAll
    public void loadDataSource(){

        try {
            expectedList = getDataFromDatabase("SELECT * FROM projects");
            if ( conn.prepareStatement("SELECT projectname FROM projects;").executeQuery() == null) {
                InputStream stream = new ClassPathResource(DATA_SOURCE_URL).getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(stream));

                for (String line; (line = bf.readLine()) != null; ) {
                    String[] lines = line.split("\t");
                    if (!lines[0].equals("Manager")) {
                        addToProjects(lines[0], Integer.parseInt(lines[1]), lines[2], Integer.parseInt(lines[3]), lines[4], Integer.parseInt(lines[5]));
                    }

                }
            }
        } catch (FileNotFoundException e){
            log.error("File not found exception: " + DATA_SOURCE_URL);
            e.printStackTrace();
        } catch (IOException e){
            log.error("IoException was found");
        }
        catch (SQLException e){
            log.error("SQL exception");
        }

    }
    //Get all records from the database
    private List<Project> getDataFromDatabase(String query) {
        List<Project> list = new ArrayList<>();
        try{
            PreparedStatement pr = conn.prepareStatement(query);
            ResultSet res = pr.executeQuery();
            while(res.next()){
                String manager = res.getString("managername");
                int managerLvl = res.getInt("managerlvl");
                String project = res.getString("projectname");
                int projectLvl = res.getInt("projectlvl");
                String company = res.getString("company");
                int risk = res.getInt("risk");
                list.add(new Project(manager, managerLvl,project, projectLvl, company, risk));
            }
            return list;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //Populating test database
    private void addToProjects(String manager, int managerLvl, String project, int projectLvl, String company, int risk){
        String query = "INSERT INTO projects (managername, managerlvl, projectname, projectlvl, company, risk) VALUES(?,?,?,?,?,?)";
        try{
            PreparedStatement pr = conn.prepareStatement(query);
            pr.setString(1, manager);
            pr.setInt(2, managerLvl);
            pr.setString(3, project);
            pr.setInt(4, projectLvl);
            pr.setString(5, company);
            pr.setInt(6, risk);
            pr.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    void listAllProject() {
        DbService service = new DbService();
        List<Project> actual = service.listAllProject();
        int x = 0;
        for (int i = 0; i < expectedList.size(); i++){
            if (!expectedList.get(i).getManagername().equals(actual.get(i).getManagername())){
                x++;
            }
            if (expectedList.get(i).getManagerlvl() != actual.get(i).getManagerlvl()){
                x++;
            }
            if (!expectedList.get(i).getProject().equals(actual.get(i).getProject())){
                x++;
            }
            if (expectedList.get(i).getProjectLvl() != actual.get(i).getProjectLvl()) {
                x++;
            }
            if (!expectedList.get(i).getCompany().equals(actual.get(i).getCompany())){
                x++;
            }
            if(expectedList.get(i).getRisk() != actual.get(i).getRisk()){
                x++;
            }
        }
        log.info("Expected: 0, Actual: " + x);
        log.info("Expected size: " + expectedList.size() + "Actual size: " + actual.size());
        assertEquals(0, x);
    }

    @Test
    void companyAndManagerCriteria() {
    }

    @Test
    void companyMaxProject() {
    }

    @Test
    void managerMaxProject() {
    }

    @Test
    void managerAndProjectLvl() {
    }

    @Test
    void updateProjectName() {
    }

    @Test
    void findByName() {
    }

    @Test
    void updateRisk() {
    }

    @Test
    void updateProjectLvl() {
    }

    @Test
    void deleteProject() {
    }
}