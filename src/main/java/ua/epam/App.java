package ua.epam;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx=new AnnotationConfigApplicationContext();
        ctx.register(SpringButchConfig.class);
        ctx.refresh();

        JobLauncher jobLauncher=(JobLauncher)ctx.getBean("jobLauncher");
        Job job=(Job)ctx.getBean("mailUserJob");
        try {
            JobExecution execution=jobLauncher.run(job,new JobParameters());
        }catch (JobExecutionException e){

        }



    }
}
