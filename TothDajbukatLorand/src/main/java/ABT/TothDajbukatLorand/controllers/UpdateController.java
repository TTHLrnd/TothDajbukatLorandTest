package ABT.TothDajbukatLorand.controllers;

import ABT.TothDajbukatLorand.model.Project;
import ABT.TothDajbukatLorand.services.DbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/update")
@RequiredArgsConstructor
@Slf4j
public class UpdateController {

    private final DbService dbService;

    @PutMapping("/projectName")
    public Project updateProjectName(@RequestBody String newName, @RequestBody String currentName){
        if (newName == null || currentName == null) {
            throw new NullPointerException("Name fields are null");
        }
        if (!newName.equals("") || !currentName.equals("")) {
            throw new IllegalStateException("Name fields are empty");
        }
        dbService.updateProjectName(newName, currentName);
        return dbService.findByName(newName);
    }

    @PutMapping("/risk")
    public Project updateRisk(@RequestBody int risk, @RequestBody String name){
        if (name == null) {
            throw new NullPointerException("Name fields are null");
        }
        dbService.updateRisk(risk, name);
        return dbService.findByName(name);
    }

    @PutMapping("/projectLvl")
    public Project updateProjectLvl(@RequestBody int projectLvl, @RequestBody String name){
        if (name == null) {
            throw new NullPointerException("Name fields are null");
        }
        dbService.updateProjectLvl(projectLvl, name);
        return dbService.findByName(name);
    }

    @PutMapping("/deleteProject")
    public Boolean deleteProject(@RequestBody String name){
        if (name == null) {
            throw new NullPointerException("Name fields are null");
        }
        return dbService.deleteProject(name);
    }
}
