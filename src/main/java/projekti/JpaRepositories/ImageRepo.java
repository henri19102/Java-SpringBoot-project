/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.JpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.Models.Image;

public interface ImageRepo extends JpaRepository<Image, Long> {
    
}
