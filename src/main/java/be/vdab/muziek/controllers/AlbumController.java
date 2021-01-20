
package be.vdab.muziek.controllers;

import be.vdab.muziek.exceptions.AlbumNietGevondenException;
import be.vdab.muziek.forms.ScoreForm;
import be.vdab.muziek.services.AlbumService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("albums")
class AlbumController {
    private final AlbumService albumService;

    AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("{id}")
    public ModelAndView album(@PathVariable long id) {
        var modelAndView = new ModelAndView("album");
        albumService.findById(id).ifPresent(album -> {
            modelAndView.addObject(album);
            modelAndView.addObject(new ScoreForm(album.getScore()));
        });
        return modelAndView;
    }
    @PostMapping("{id}/score")
    public ModelAndView wijzigScore(@PathVariable long id, @Valid ScoreForm form,
                                    Errors errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            var modelAndView = new ModelAndView("album");
            albumService.findById(id).ifPresent(
                    album -> modelAndView.addObject(album));
            return modelAndView;
        }
        try {
            albumService.wijzigScore(id, form.getScore());
            redirect.addAttribute("id", id);
            return new ModelAndView("redirect:/albums/{id}");
        } catch (AlbumNietGevondenException ex) {
            return new ModelAndView("album");
        }
    }
}