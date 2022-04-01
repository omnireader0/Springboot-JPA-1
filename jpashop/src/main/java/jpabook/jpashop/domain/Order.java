package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;


    /* 회원 변경이 일어나는 경우, Member 클래스에서는 orders 리스트를, Order에서는 member를
    변경할 수 있다. 즉 객체 변경 일어나는 포인트가 두 곳인데, JPA에서는 한개만 변경하면 둘 다
    수정된다. 그렇다면 어디를 변경할까? 연관관계 주인인 Orders 엔티티의 외래키인 MEMBER_ID를 변경한다.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 주문 회원

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 주문 상품 엔티티의 order에 매핑됨
    private List<OrderItem> orderItems = new ArrayList<>();

    // 1:1 관계는 fk키를 둘 다 둘 수 있지만, 접근이 많은 곳을 연관관계 주인으로 하기
    @OneToOne(cascade = CascadeType.ALL, fetch =FetchType.LAZY )
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 배송 정보

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
