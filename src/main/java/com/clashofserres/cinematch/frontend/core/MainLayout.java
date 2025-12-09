package com.clashofserres.cinematch.frontend.core;

import com.clashofserres.cinematch.frontend.view.LoginView;
import com.clashofserres.cinematch.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
// TODO: Bring version from Jurney or create a completely new one
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private H1 viewTitle;
    private boolean isDarkTheme = true; // Dark theme is default

    UserService userService;
    AuthenticationContext authenticationContext;

    public MainLayout(UserService userService, AuthenticationContext authenticationContext) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        // Title (left)
        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // User menu (right)
        HorizontalLayout userMenuHolder = new HorizontalLayout(createUserMenuOrLoginButton());
        userMenuHolder.setSpacing(true);
        userMenuHolder.setAlignItems(FlexComponent.Alignment.CENTER);

        // Header layout (controls left/right alignment)
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Left side (toggle + title)
        HorizontalLayout left = new HorizontalLayout(toggle, viewTitle);
        left.setAlignItems(FlexComponent.Alignment.CENTER);

        header.add(left, userMenuHolder);

        addToNavbar(true, header);
    }


    private void addDrawerContent() {
        Span appName = new Span("CineMatch");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Icon lightbulb = VaadinIcon.LIGHTBULB.create();
        Icon moon = VaadinIcon.MOON_O.create();

        // Dark theme is default
        Button themeToggle = new Button(lightbulb);
        themeToggle.getStyle().setScale("1.1");
        themeToggle.addClassNames("offset-hovered-element");
        themeToggle.setTooltipText("Toggle light/dark theme");
        themeToggle.addThemeVariants(ButtonVariant.LUMO_ICON);

        themeToggle.addClickListener(e -> {
            isDarkTheme = !isDarkTheme;
            if (isDarkTheme) {
                themeToggle.setIcon(lightbulb);
            } else {
                themeToggle.setIcon(moon);
            }

            setTheme(isDarkTheme);
        });

        layout.add(themeToggle);


        return layout;
    }

    private Component createUserMenuOrLoginButton() {

        var avatar = new Avatar();
        var userOptional = userService.getMyUserOptional();
        if (!userOptional.isPresent()) {

            Button loginButton = new Button(VaadinIcon.SIGN_IN.create());
            loginButton.setText("Log In");
            loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            loginButton.addClickListener(e -> {
                UI.getCurrent().navigate(LoginView.class);
            });
            return loginButton;
        }

        var user = userOptional.get();
        avatar = new Avatar(user.getUsername());
        avatar.setColorIndex(user.getId().intValue());

        VerticalLayout avatarHolder = new VerticalLayout(avatar);
        avatarHolder.setAlignItems(FlexComponent.Alignment.CENTER);

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames(LumoUtility.Margin.MEDIUM);

        var userMenuItem = userMenu.addItem(avatarHolder);
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames(LumoUtility.Margin.Right.SMALL);

        userMenuItem.getSubMenu().addItem("Profile", menuItemClickEvent -> {
            UI.getCurrent().navigate("profile");
            // TODO: switch to this
            //UI.getCurrent().navigate(ProfileView.class));
        });
        userMenuItem.getSubMenu().addItem("Logout", menuItemClickEvent -> {
            authenticationContext.logout();
        });


        return userMenu;
    }

    private void setTheme(boolean dark)
    {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
