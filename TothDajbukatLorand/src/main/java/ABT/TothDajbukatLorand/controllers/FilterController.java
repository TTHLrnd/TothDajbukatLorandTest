package ABT.TothDajbukatLorand.controllers;

import ABT.TothDajbukatLorand.model.Project;
import ABT.TothDajbukatLorand.services.DbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
@Slf4j
public class FilterController {

    private final DbService dbService;
    @GetMapping("/all")
    public List<Project> all(){
        return dbService.listAllProject();
    }

    @GetMapping("/criteria1")
    public List<Project> companyAndManager(){
        return dbService.companyAndManagerCriteria();
    }

    @GetMapping("/criteria2")
    public List<Project> companyMaxProject(){
        return dbService.companyMaxProject();
    }

    @GetMapping("/criteria3")
    public List<Project> managerMaxProject(){
        return dbService.managerMaxProject();
    }

    @GetMapping("/criteria4")
    public List<Project> managerAndProjectLvl(){
        return dbService.managerAndProjectLvl();
    }

    @GetMapping("/loadData")
    public void loadData(){
        dbService.loadDataSource();
    }
}
