/*
package job.hamo.library.dto;

import job.hamo.library.entity.JobStatus;
import job.hamo.library.job.Job;

public class JobDTO {
    private Long id;
    private String name;
    private Object parameters;
    private JobStatus status;

    public JobDTO(Long id, String name, Object parameters, JobStatus status) {
        this.id = id;
        this.name = name;
        this.parameters = parameters;
        this.status = status;
    }

    public JobDTO(String name, Object parameters, JobStatus status) {
        this.name = name;
        this.parameters = parameters;
        this.status = status;
    }

    public static JobDTO fromJob(Job job) {
        return new JobDTO(job.getId(), job.getName(), job.getParameters(), job.getStatus());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
*/
