package ua.epam;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.mail.SimpleMailMessageItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import ua.epam.model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
@Import({DataSourceConfig.class, MailSenderConfig.class})
@EnableBatchProcessing
public class SpringButchConfig extends DefaultBatchConfigurer {


    @Autowired
    private DataSource dataSource;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public JdbcCursorItemReader<User> reader() {
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<User>();
        reader.setDataSource(dataSource);
        reader.setSql("Select name, balance, mail from bills");
        reader.setRowMapper(new UserRowMapper());
        return reader;
    }


    public class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setName(resultSet.getString("name"));
            user.setBalance(resultSet.getLong("balance"));
            user.setMail(resultSet.getString("mail"));
            return user;
        }
    }

    @Bean
    public UserMailItemProcessor processor() {
        return new UserMailItemProcessor();
    }

    @Bean
    public SimpleMailMessageItemWriter writer() {
        SimpleMailMessageItemWriter writer = new SimpleMailMessageItemWriter();
        writer.setMailSender(sender);
        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<User, SimpleMailMessage>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job mailUserJob() {
        return jobBuilderFactory.get("mailUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = null;
    }
}