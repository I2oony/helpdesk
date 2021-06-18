package services;

import entites.Operator;
import entites.Ticket;

import java.util.ArrayList;
import java.util.TimerTask;

public class TicketsQueueChecker extends TimerTask {
    static CustomLogger logger = new CustomLogger(TicketsQueueChecker.class.getName());

    @Override
    public void run() {
        logger.info("Checking the ticket's queue...");
        Ticket[] tickets = DBConnect.getTicketsInQueue();
        try {
            int length = tickets.length;
            logger.info("Count of tickets in the queue: " + length);
        } catch (Exception e) {
            logger.info("No ticket in the queue now.");
        }
        for (Ticket ticket : tickets) {
            if (ticket.getState().equals("waiting")) {
                checkTicketPriority(ticket);
            } else {
                ticket.setPriority(0);
            }
            DBConnect.updateTicket(ticket);
        }
    }

    public static void checkTicketPriority(Ticket ticket) {
        // Is ticket in the queue? If priority equals 0 - no, otherwise - yes.
        if (ticket.getPriority() == 0) {
            ticket.setPriority(1);
        }

        // TODO find bug of assign
        ArrayList<Operator> onlineOperators = DBConnect.getOnlineOperatorsList();
        Operator freeOperator = findFreeOperator(onlineOperators);
        // If ticket already has active operator - just increase priority for nex iteration
        if (hasActiveOperator(ticket, onlineOperators)) {
            ticket.setPriority(ticket.getPriority() + 1);
        // If ticket has no operator and there is exists free online operator - assign ticket to this operator
        } else if (freeOperator!=null) {
            ticket.addOperator(freeOperator.getUsername());
            try {
                freeOperator.pushTicket(ticket.getId());
                DBConnect.updateOperator(freeOperator);
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
            ticket.setPriority(ticket.getPriority() + 1);
        // If ticket can't be assigned to any operator -
        // increase it's priority by 3 to make the new ticket without any answer yet assigns earlier then others
        } else {
            ticket.setPriority(ticket.getPriority() + 3);
        }
        DBConnect.updateTicket(ticket);
    }

    public static boolean hasActiveOperator(Ticket ticket, ArrayList<Operator> onlineOperators) {
        ArrayList<String> ticketOperators = ticket.getOperators();
        if (ticketOperators.size() != 0) {
            for (String ticketOperator : ticketOperators) {
                Operator currentOperator = DBConnect.getOperatorStatus(ticketOperator);
                if (onlineOperators.contains(currentOperator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Operator findFreeOperator(ArrayList<Operator> operators) {
        logger.info("Trying to find free operator...");
        if (operators.size()!=0) {
            Operator currentOperator = operators.get(0);
            for (Operator operator : operators) {
                if (operator.getTicketsCount() < 5) {
                    if (operator.getTicketsCount() < currentOperator.getTicketsCount()) {
                        currentOperator = operator;
                    }
                }
            }
            if (currentOperator.getTicketsCount() < 5) {
                return currentOperator;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
