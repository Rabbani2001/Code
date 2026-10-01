package com.schooltech.sms.dao.client.circulars;


import com.schooltech.sms.entity.client.circulars.BusCircular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BusCircularRepository extends JpaRepository<BusCircular, Long> {

    Optional<BusCircular> findByBusRoute(String route);

    List<BusCircular> findBySession(String session);

    @Query("SELECT e FROM BusCircular e WHERE e.busRoute= :busRoute AND e.session= :session")
    Optional<BusCircular> findByBusRouteAndSession(@Param("busRoute") String busRoute, @Param("session") String session);


    @Transactional
    void deleteByBusRoute(String className);

    boolean existsByBusRoute(String className);

    @Query("SELECT t.busRoute FROM BusCircular t WHERE t.session= :session")
    List<String> findAllBusRoutesBySession(@Param("session") String session);
}
