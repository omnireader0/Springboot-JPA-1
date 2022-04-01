package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    /* id는 엔티티 식별자, member_id는 pk 컬럼명이다.
    member_id는 테이블명+id 이런 의미. 테이블 타입이 없어서 구분해주기 위함
   * */
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // Order엔티티의 member에 매핑됨
    private List<Order> orders = new ArrayList<>();
}
