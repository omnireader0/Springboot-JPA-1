package jpabook.jpashop.repository;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // jpa에서 제공하는 메서드, 영속성컨텍스트에 멤버를 저장
    public void save(Member member) {
        em.persist(member);
    }

    // jpa에서 제공하는 ~, id값을 넘기면 멤버 찾아 반환
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // jpql과 반환 타입을 넘기면, 멤버를 리스트로 만들어서 리턴
    // jpql은 sql과 비슷. sql은 테이블을 쿼리, jpql은 엔티티를 쿼리함
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
    // 이름으로 회원 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}