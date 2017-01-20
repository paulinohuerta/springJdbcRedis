package sample.spring.chapter12.dao;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;


import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// posible a quitar alguno
import java.util.ArrayList;
import java.util.List;

import sample.spring.chapter12.domain.Correo;
import sample.spring.chapter12.domain.FixedDepositDetails;

@Repository
public class FixedDepositDaoImpl implements FixedDepositDao {
        private static String Correo_KEY = "Correo";
        @Autowired
        private JdbcTemplate jdbcTemplate;
        @Autowired
        private RedisTemplate<String, String> redisTemplate;
	private List<FixedDepositDetails> fdList;
	public FixedDepositDaoImpl() {
		fdList = new ArrayList<FixedDepositDetails>();
	}
        @PostConstruct
        public void init(){
	        List<FixedDepositDetails> fdList1;
                fdList1 = new ArrayList<FixedDepositDetails>();
                fdList=findAll(); 
                for(FixedDepositDetails f : fdList){
                    String id = String.valueOf(f.getId());
                    Correo c = (Correo) redisTemplate.opsForHash().get(Correo_KEY,id);
                    f.setEmail(c.getEmail());
                    fdList1.add(f);
                }
		fdList = new ArrayList<FixedDepositDetails>(fdList1);
        }
        public void setJdbcTemplate(JdbcTemplate t) {
          jdbcTemplate=t;
        }
        private List<FixedDepositDetails> findAll() {
                String sql = "select fixed_deposit_id,amount,tenure from fixed_deposit_details";
        //        List<FixedDepositDetails> laList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<FixedDepositDetails>(FixedDepositDetails.class));
          // mapeo manual
          List<FixedDepositDetails> laList = jdbcTemplate.query(sql,new RowMapper<FixedDepositDetails>() {
             public FixedDepositDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
               FixedDepositDetails f = new FixedDepositDetails();
               f.setId(Long.parseLong(rs.getString(1)));
               f.setDepositAmount(rs.getString(2));
               f.setTenure(rs.getString(3));
               f.setEmail("");
               return f;
             }
           });
           return laList;
        }

	@Override
	public List<FixedDepositDetails> getFixedDeposits() {
		return fdList;
	}

	@Override
	public void saveFixedDeposit(FixedDepositDetails fixedD) {
                String sql0 = "select auto_increment from information_schema.tables where table_name = 'fixed_deposit_details'";
    	        String incr =(String) jdbcTemplate.queryForObject(sql0, String.class);
                String sql = "insert into fixed_deposit_details(account_id, amount,tenure,fd_creation_date) values (2,?,?,\"2017-01-11\")";
                jdbcTemplate.update(sql, Integer.parseInt(fixedD.getDepositAmount()), Integer.parseInt(fixedD.getTenure()));
                String string = fixedD.getEmail();
                String[] parts = string.split("@");
                Correo uncorreo = new Correo(incr,parts[0],string);
                redisTemplate.opsForHash().put(Correo_KEY, incr, uncorreo);

		long id = Long.parseLong(incr);
		fixedD.setId(id);
		fdList.add(fixedD);
	}

	public void closeFixedDeposit(int fixedDepositId) {
		for (FixedDepositDetails fixedDepositDetails : fdList) {
			if (fixedDepositDetails.getId() == fixedDepositId) {
				fdList.remove(fixedDepositDetails);
                                String sql = "delete from  fixed_deposit_details where fixed_deposit_id=?";
                                jdbcTemplate.update(sql, (long)fixedDepositId);
                                String st = String.valueOf(fixedDepositId);
                                redisTemplate.opsForHash().delete(Correo_KEY,st);
				break;
			}
		}
	}

	public FixedDepositDetails getFixedDeposit(int fixedDepositId) {
		FixedDepositDetails matchingFixedDepositDetails = null;
		for (FixedDepositDetails fixedDepositDetails : fdList) {
			if (fixedDepositDetails.getId() == fixedDepositId) {
				matchingFixedDepositDetails = fixedDepositDetails;
				break;
			}
		}
		return matchingFixedDepositDetails;
	}

	public void editFixedDeposit(FixedDepositDetails modifiedFixedDepositDetails) {
		for (FixedDepositDetails fixedDepositDetails : fdList) {
			if (fixedDepositDetails.getId() == modifiedFixedDepositDetails
					.getId()) {
				fdList.remove(fixedDepositDetails);
				break;
			}
		}
		fdList.add(modifiedFixedDepositDetails);
	}
}
