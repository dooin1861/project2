package du.db;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberMapper {
	@Select("select count(*) from members where email = #{email} and password = #{password}")
	public int isMember(MemberDto memberDto); // MemberDto가 각각 email, password에 값을 넣어줌.
	
	@Select("select * from members where email = #{email} and password = #{password}")
	public MemberDto findMember(MemberDto memberDto);
	
	@Insert("insert into members (member_id, email, password, name) values (members_seq.nextval, #{email}, #{password}, #{name})")
    public void insertMember(MemberDto memberDto);
	
	@Update("update members set email = #{email}, password = #{password}, name = #{name} where member_id = #{member_id}")
	public void updateMember(MemberDto memberDto);
}
