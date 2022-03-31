package ABT.TothDajbukatLorand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Reference;

import javax.persistence.*;


@Getter @Setter
@AllArgsConstructor
public class Project {
    private String managername;
    private int managerlvl;
    private String project;
    private int projectLvl;
    private String company;
    private int risk;
}
