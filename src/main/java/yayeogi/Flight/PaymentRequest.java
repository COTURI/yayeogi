package yayeogi.Flight;

public class PaymentRequest {
    private String orderId;
    private String userId;
    private String itemName;
    private int quantity;
    private int totalAmount;
    private int taxFreeAmount;

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTaxFreeAmount() {
        return taxFreeAmount;
    }

    public void setTaxFreeAmount(int taxFreeAmount) {
        this.taxFreeAmount = taxFreeAmount;
    }
}
