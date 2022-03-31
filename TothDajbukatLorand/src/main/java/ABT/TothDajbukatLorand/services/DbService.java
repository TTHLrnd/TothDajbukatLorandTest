package ABT.TothDajbukatLorand.services;

import ABT.TothDajbukatLorand.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DbService {

    private final String DATA_SOURCE_URL = "datasource.txt";
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/sqlmassdata";
    private final String DATABASE_USERNAME = System.getenv("DB_USER");
    private final String DATABASE_PASSWORD = System.getenv("DB_PASS");
    Connection conn;

    public DbService(){
        conn = connect();
    }
    private Connection connect(){
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    /*Reading projects from file*/
    public void loadDataSource(){
        try {
            InputStream stream = new ClassPathResource(DATA_SOURCE_URL).getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(stream));

            for (String line; (line = bf.readLine()) != null;) {
                String[] lines = line.split("\t");
                if (!lines[0].equals("Manager")) {
                    addToProjects(lines[0],Integer.parseInt(lines[1]), lines[2], Integer.parseInt(lines[3]), lines[4], Integer.parseInt(lines[5]));
                }

            }
        } catch (FileNotFoundException e){
            log.error("File not found exception: " + DATA_SOURCE_URL);
            e.printStackTrace();
        } catch (IOException e){
            log.error("IoException was found");
        }

    }

    /*Adding projects to the database by the read in lines from file*/
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
    /*Listing all existing project in the database*/
    public List<Project> listAllProject(){
        String query = "SELECT * FROM projects";
        return new ArrayList<>(getDataFromDatabase(query));
    }
    /*Listing projects that do not meet criteria 1*/
    public List<Project> companyAndManagerCriteria(){
        String query = "SELECT * FROM projects GROUP BY managername, company HAVING COUNT(*) > 1;";
        return new ArrayList<>(getDataFromDatabase(query));

    }
    /*Listing projects that do not meet criteria 2*/
    public List<Project> companyMaxProject(){
        String query = "SELECT * FROM projects GROUP BY company HAVING COUNT(*) > 6;";
        return new ArrayList<>(getDataFromDatabase(query));
    }
    /*Listing projects that do not meet criteria 3*/
    public List<Project> managerMaxProject(){
        String query = "SELECT * FROM projects GROUP BY managername HAVING COUNT(*) > 4;";
        return new ArrayList<>(getDataFromDatabase(query));
    }
    /*Listing projects that do not meet criteria 4*/
    public List<Project> managerAndProjectLvl(){
        String query = "SELECT * FROM projects WHERE NOT projectlvl-projects.managerlvl <= 2;";
        return new ArrayList<>(getDataFromDatabase(query));
    }

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

    public void updateProjectName(String newName, String currentName) {
        String query = "UPDATE projects SET projectname = ? WHERE projectname = ?;";
        updateData(query, newName, currentName);
    }
    /*Updating project name*/
    private void updateData(String query, String newName, String currentName){
        try{
            PreparedStatement pr = conn.prepareStatement(query);
            pr.setString(1, newName);
            pr.setString(2, currentName);
            pr.executeQuery();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*Updating risk and projectLvl*/
    private void updateData(String query, int risk, String name){
        try{
            PreparedStatement pr = conn.prepareStatement(query);
            pr.setInt(1, risk);
            pr.setString(2, name);
            pr.executeQuery();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Project findByName(String name) {
        String query = "SELECT * FROM projects WHERE projectname = ?";
        try {
            PreparedStatement pr = connect().prepareStatement(query);
            pr.setString(1,name);
            ResultSet res = pr.executeQuery();
                return new Project(
                        res.getString("managername"),
                        res.getInt("managerlvl"),
                        res.getString("projectname"),
                        res.getInt("projectlvl"),
                        res.getString("company"),
                        res.getInt("risk"));
        }catch (SQLException e){
            log.error("Project was not found!");
            e.printStackTrace();
            return null;
        }
    }

    public void updateRisk(int risk, String name) {
        String query = "UPDATE projects SET risk = ? WHERE projectname = ?;";
        updateData(query, risk, name);
    }

    public void updateProjectLvl(int projectLvl, String name) {
        String query = "UPDATE projects SET projectlvl = ? WHERE projectname = ?;";
        updateData(query, projectLvl, name);
    }

    public Boolean deleteProject(String name) {
        String query = "DELETE FROM projects WHERE projectname = ?;";
        deleteData(query, name);
        return findByName(name) == null;
    }

    private void deleteData(String query, String name) {
        try{
            PreparedStatement pr = conn.prepareStatement(query);
            pr.setString(1, name);
            pr.executeQuery();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
