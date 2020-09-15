package myProject_LSP;

import javax.persistence.*;

import myProject_LSP.external.Gift;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer restaurantId;
    private Integer restaurantMenuId;
    private Integer customerId;
    private Integer qty;
    private Long modifiedDate;
    private String status;
    //수정
    private String giftStatus;

    @PrePersist
    public void onPrePersist(){

        if(!"ORDER : COOK CANCELED".equals(this.getStatus())){


            this.setStatus("ORDER : ORDERED");
        }
        this.setModifiedDate(System.currentTimeMillis());
    }

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);

        System.out.println(ordered.getStatus()+ "#######################33");
        if(!"ORDER : COOK CANCELED".equals(ordered.getStatus())){
            ordered.publishAfterCommit();

            /*수정*/
            Gift gift = new Gift();
            gift.setOrderId(this.getId());
            gift.setStatus("ORDER : GIFT SEND");

            OrderApplication.applicationContext.getBean(myProject_LSP.external.GiftService.class).giftSend(gift);
        }

    }

    @PreRemove
    public void onPreRemove(){
        OrderCancelled orderCancelled = new OrderCancelled();
        this.setStatus("ORDER : ORDER CANCELED");
        BeanUtils.copyProperties(this, orderCancelled);

        orderCancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        myProject_LSP.external.Cancellation cancellation = new myProject_LSP.external.Cancellation();
        // mappings goes here
        //추가부분
        cancellation.setOrderId(this.getId());
        //cancellation.setCustomerId(this.getCustomerId());
        BeanUtils.copyProperties(this, cancellation);

        //cancellation.setStatus("ORDER : ORDER CANCELED");


        OrderApplication.applicationContext.getBean(myProject_LSP.external.CancellationService.class)
            .cancel(cancellation);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }
    public Integer getRestaurantMenuId() {
        return restaurantMenuId;
    }

    public void setRestaurantMenuId(Integer restaurantMenuId) {
        this.restaurantMenuId = restaurantMenuId;
    }
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getGiftStatus() {
        return giftStatus;
    }

    public void setGiftStatus(String giftStatus) {
        this.giftStatus = giftStatus;
    }
}
